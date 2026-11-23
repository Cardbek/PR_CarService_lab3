CREATE TABLE IF NOT EXISTS clients (
                                       id UUID PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS mechanics (
                                         id UUID PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
    is_busy BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS cars (
                                    vin VARCHAR(255) PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    owner_id UUID REFERENCES clients(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS repair_orders (
                                             id UUID PRIMARY KEY,
                                             car_vin VARCHAR(255) REFERENCES cars(vin),
    mechanic_id UUID REFERENCES mechanics(id),
    status VARCHAR(50) NOT NULL,
    completed_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS service_jobs (
                                            id UUID PRIMARY KEY,
                                            repair_order_id UUID REFERENCES repair_orders(id) ON DELETE CASCADE,
    description VARCHAR(255) NOT NULL,
    parts_cost DECIMAL(10, 2) NOT NULL,
    labor_cost DECIMAL(10, 2) NOT NULL,
    is_done BOOLEAN DEFAULT FALSE
    );