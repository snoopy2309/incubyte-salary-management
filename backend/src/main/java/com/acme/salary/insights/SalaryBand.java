package com.acme.salary.insights;

/**
 * One bar of the salary distribution histogram: how many employees fall in a
 * given USD pay band.
 *
 * @param label     human-readable band (e.g. "$75k–$100k")
 * @param headcount employees whose USD salary falls in the band
 */
public record SalaryBand(String label, long headcount) {
}
