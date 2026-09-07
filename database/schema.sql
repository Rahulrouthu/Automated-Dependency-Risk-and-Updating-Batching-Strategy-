-- =============================================================================
-- Automated Dependency Update Risk Assessment and Batching Strategy Database
-- Target Engine: MySQL 8.0+
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `dependency_risk_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `dependency_risk_db`;

-- Repositories Table
CREATE TABLE IF NOT EXISTS `repositories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `owner` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `url` VARCHAR(1000) NOT NULL,
    `default_branch` VARCHAR(100) DEFAULT 'main',
    `detected_ecosystems` VARCHAR(500),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `last_scanned_at` DATETIME,
    INDEX `idx_repo_owner_name` (`owner`, `name`),
    INDEX `idx_repo_url` (`url`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dependency Manifest Files
CREATE TABLE IF NOT EXISTS `dependency_files` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `repository_id` BIGINT NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `ecosystem` ENUM('MAVEN', 'NPM', 'PYTHON', 'GRADLE') NOT NULL,
    `dependency_count` INT DEFAULT 0,
    CONSTRAINT `fk_depfile_repo` FOREIGN KEY (`repository_id`) REFERENCES `repositories` (`id`) ON DELETE CASCADE,
    INDEX `idx_depfile_repo_id` (`repository_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dependencies
CREATE TABLE IF NOT EXISTS `dependencies` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `dependency_file_id` BIGINT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `group_or_namespace` VARCHAR(255),
    `current_version` VARCHAR(100) NOT NULL,
    `latest_version` VARCHAR(100),
    `version_diff_type` VARCHAR(50) DEFAULT 'UNKNOWN',
    `ecosystem` ENUM('MAVEN', 'NPM', 'PYTHON', 'GRADLE') NOT NULL,
    `scope` VARCHAR(50) DEFAULT 'compile',
    `is_direct` BOOLEAN DEFAULT TRUE,
    `is_dev` BOOLEAN DEFAULT FALSE,
    `is_deprecated` BOOLEAN DEFAULT FALSE,
    `repository_url` VARCHAR(1000),
    `homepage_url` VARCHAR(1000),
    `changelog_url` VARCHAR(1000),
    `documentation_url` VARCHAR(1000),
    `migration_guide_url` VARCHAR(1000),
    CONSTRAINT `fk_dep_file` FOREIGN KEY (`dependency_file_id`) REFERENCES `dependency_files` (`id`) ON DELETE CASCADE,
    INDEX `idx_dep_name` (`name`),
    INDEX `idx_dep_eco` (`ecosystem`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Vulnerabilities (OSV / GHSA / NVD)
CREATE TABLE IF NOT EXISTS `vulnerabilities` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `dependency_id` BIGINT NOT NULL,
    `vuln_id` VARCHAR(100) NOT NULL,
    `summary` VARCHAR(1000),
    `details` TEXT,
    `severity` ENUM('CRITICAL', 'HIGH', 'MODERATE', 'LOW', 'UNKNOWN') DEFAULT 'UNKNOWN',
    `cvss_score` DOUBLE,
    `affected_range` VARCHAR(255),
    `fixed_version` VARCHAR(100),
    `reference_url` VARCHAR(1000),
    CONSTRAINT `fk_vuln_dep` FOREIGN KEY (`dependency_id`) REFERENCES `dependencies` (`id`) ON DELETE CASCADE,
    INDEX `idx_vuln_id` (`vuln_id`),
    INDEX `idx_vuln_sev` (`severity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5-Dimensional Risk Assessments
CREATE TABLE IF NOT EXISTS `risk_assessments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `dependency_id` BIGINT NOT NULL UNIQUE,
    `total_score` DOUBLE NOT NULL,
    `risk_level` ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    `version_risk` DOUBLE DEFAULT 0,
    `security_risk` DOUBLE DEFAULT 0,
    `compatibility_risk` DOUBLE DEFAULT 0,
    `dependency_impact_risk` DOUBLE DEFAULT 0,
    `build_risk` DOUBLE DEFAULT 0,
    `explanation_json` TEXT,
    `recommendation_text` TEXT,
    CONSTRAINT `fk_risk_dep` FOREIGN KEY (`dependency_id`) REFERENCES `dependencies` (`id`) ON DELETE CASCADE,
    INDEX `idx_risk_level` (`risk_level`),
    INDEX `idx_risk_score` (`total_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Safe Dependency Batches
CREATE TABLE IF NOT EXISTS `dependency_batches` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `repository_id` BIGINT NOT NULL,
    `batch_number` INT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `category` ENUM('URGENT_SECURITY', 'SAFE_PATCHES', 'MINOR_UPDATES', 'ISOLATED_MAJOR') NOT NULL,
    `batch_risk_level` ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    `batch_risk_score` DOUBLE NOT NULL,
    `strategy_description` TEXT,
    `execution_commands` TEXT,
    `pr_title` VARCHAR(500),
    `pr_body` TEXT,
    CONSTRAINT `fk_batch_repo` FOREIGN KEY (`repository_id`) REFERENCES `repositories` (`id`) ON DELETE CASCADE,
    INDEX `idx_batch_repo` (`repository_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Batch Items Association
CREATE TABLE IF NOT EXISTS `batch_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `batch_id` BIGINT NOT NULL,
    `dependency_id` BIGINT NOT NULL,
    `action_description` VARCHAR(500),
    CONSTRAINT `fk_bitem_batch` FOREIGN KEY (`batch_id`) REFERENCES `dependency_batches` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_bitem_dep` FOREIGN KEY (`dependency_id`) REFERENCES `dependencies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Analysis Runs History
CREATE TABLE IF NOT EXISTS `analysis_runs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `repository_id` BIGINT NOT NULL,
    `status` ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    `total_dependencies` INT DEFAULT 0,
    `outdated_count` INT DEFAULT 0,
    `vulnerability_count` INT DEFAULT 0,
    `critical_count` INT DEFAULT 0,
    `high_count` INT DEFAULT 0,
    `batch_count` INT DEFAULT 0,
    `overall_risk_score` DOUBLE DEFAULT 0,
    `overall_risk_level` VARCHAR(50) DEFAULT 'LOW',
    `error_message` VARCHAR(1000),
    `started_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `completed_at` DATETIME,
    CONSTRAINT `fk_run_repo` FOREIGN KEY (`repository_id`) REFERENCES `repositories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
