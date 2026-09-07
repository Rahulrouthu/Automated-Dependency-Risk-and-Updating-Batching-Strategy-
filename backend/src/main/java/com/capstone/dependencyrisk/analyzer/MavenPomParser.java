package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.DependencyFileEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class MavenPomParser implements DependencyParser {

    @Override
    public EcosystemType getSupportedEcosystem() {
        return EcosystemType.MAVEN;
    }

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String normalized = filePath.toLowerCase();
        return normalized.endsWith("pom.xml") || normalized.endsWith("pom.xml.template");
    }

    @Override
    public List<DependencyEntity> parse(String content, String filePath, DependencyFileEntity fileEntity) {
        List<DependencyEntity> dependencies = new ArrayList<>();
        if (content == null || content.trim().isEmpty()) {
            return dependencies;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setNamespaceAware(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            // 1. Extract Properties
            Map<String, String> properties = new HashMap<>();
            NodeList propertiesNodes = doc.getElementsByTagName("properties");
            if (propertiesNodes.getLength() > 0) {
                Element propsElem = (Element) propertiesNodes.item(0);
                NodeList childNodes = propsElem.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        properties.put(node.getNodeName(), node.getTextContent().trim());
                    }
                }
            }

            // 2. Extract Direct Dependencies
            NodeList depNodes = doc.getElementsByTagName("dependency");
            for (int i = 0; i < depNodes.getLength(); i++) {
                Node node = depNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element depElem = (Element) node;
                    
                    String groupId = getElementText(depElem, "groupId");
                    String artifactId = getElementText(depElem, "artifactId");
                    String version = getElementText(depElem, "version");
                    String scope = getElementText(depElem, "scope");
                    String optional = getElementText(depElem, "optional");

                    if (artifactId == null || artifactId.trim().isEmpty()) {
                        continue;
                    }

                    String declaredRange = version != null ? version.trim() : "RELEASE";

                    // Resolve properties in version: e.g. ${spring.version}
                    if (version != null && version.startsWith("${") && version.endsWith("}")) {
                        String propKey = version.substring(2, version.length() - 1);
                        if (properties.containsKey(propKey)) {
                            version = properties.get(propKey);
                        }
                    }

                    // Default version fallback if managed by parent POM / BOM
                    if (version == null || version.trim().isEmpty()) {
                        version = "RELEASE";
                    }

                    DependencyEntity entity = new DependencyEntity();
                    entity.setDependencyFile(fileEntity);
                    entity.setEcosystem(EcosystemType.MAVEN);
                    entity.setGroupOrNamespace(groupId != null ? groupId.trim() : "org.apache");
                    entity.setName(artifactId.trim());
                    entity.setDeclaredVersionRange(declaredRange);
                    entity.setCurrentVersion(cleanVersion(version));
                    entity.setResolvedVersion(cleanVersion(version));
                    entity.setLockfileSource("pom.xml");
                    entity.setScope(scope != null && !scope.trim().isEmpty() ? scope.trim() : "compile");
                    entity.setDirect(true);
                    entity.setDev("test".equalsIgnoreCase(scope) || "provided".equalsIgnoreCase(scope));

                    dependencies.add(entity);
                }
            }
        } catch (Exception e) {
            // Log and fallback to regex-based robust parsing
            dependencies.addAll(parseMavenWithRegex(content, fileEntity));
        }

        return deduplicate(dependencies);
    }

    private String getElementText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0 && list.item(0).getParentNode() == parent) {
            return list.item(0).getTextContent();
        }
        return null;
    }

    private List<DependencyEntity> parseMavenWithRegex(String content, DependencyFileEntity fileEntity) {
        List<DependencyEntity> list = new ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "<dependency>[\\s\\S]*?<groupId>([\\w.\\-]+)</groupId>[\\s\\S]*?<artifactId>([\\w.\\-]+)</artifactId>(?:[\\s\\S]*?<version>([\\w.\\-${}]+)</version>)?",
                java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String group = matcher.group(1);
            String artifact = matcher.group(2);
            String version = matcher.group(3) != null ? matcher.group(3) : "1.0.0";
            
            DependencyEntity entity = new DependencyEntity();
            entity.setDependencyFile(fileEntity);
            entity.setEcosystem(EcosystemType.MAVEN);
            entity.setGroupOrNamespace(group);
            entity.setName(artifact);
            entity.setDeclaredVersionRange(version);
            entity.setCurrentVersion(cleanVersion(version));
            entity.setResolvedVersion(cleanVersion(version));
            entity.setLockfileSource("pom.xml");
            entity.setScope("compile");
            entity.setDirect(true);
            list.add(entity);
        }
        return list;
    }

    private String cleanVersion(String raw) {
        if (raw == null) return "1.0.0";
        String v = raw.trim();
        if (v.startsWith("[") || v.startsWith("(")) {
            v = v.replaceAll("[\\[\\]()]", "").split(",")[0].trim();
        }
        return v.isEmpty() ? "1.0.0" : v;
    }

    private List<DependencyEntity> deduplicate(List<DependencyEntity> list) {
        Map<String, DependencyEntity> map = new LinkedHashMap<>();
        for (DependencyEntity d : list) {
            String key = d.getCoordinates();
            map.putIfAbsent(key, d);
        }
        return new ArrayList<>(map.values());
    }
}
