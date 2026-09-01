-- Soft-delete support: employees are deactivated, not hard-deleted.
ALTER TABLE employees ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX idx_employees_active ON employees (active);
