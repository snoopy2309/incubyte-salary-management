-- Runs once when the Postgres volume is first initialised.
-- Creates a separate database for the automated tests so they never collide
-- with the application/demo data in the main "salary" database.
CREATE DATABASE salary_test;
