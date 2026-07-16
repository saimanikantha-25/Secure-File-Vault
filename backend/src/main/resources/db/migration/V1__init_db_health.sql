CREATE TABLE system_health_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL
);

INSERT INTO system_health_check (status) VALUES ('HEALTHY');
