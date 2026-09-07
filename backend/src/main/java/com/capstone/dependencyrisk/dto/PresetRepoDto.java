package com.capstone.dependencyrisk.dto;

import com.capstone.dependencyrisk.entity.EcosystemType;

public class PresetRepoDto {
    private String name;
    private String url;
    private String description;
    private EcosystemType ecosystem;
    private String tag;

    public PresetRepoDto() {}

    public PresetRepoDto(String name, String url, String description, EcosystemType ecosystem, String tag) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.ecosystem = ecosystem;
        this.tag = tag;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public EcosystemType getEcosystem() { return ecosystem; }
    public void setEcosystem(EcosystemType ecosystem) { this.ecosystem = ecosystem; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
