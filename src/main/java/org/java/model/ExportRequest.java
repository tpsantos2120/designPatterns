package org.java.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request object for export endpoint.
 *
 * @param sales  List of sales to export (must not be null or empty)
 * @param format Export format (must not be null)
 */
public record ExportRequest(
    @NotNull(message = "Sales list cannot be null")
    @NotEmpty(message = "Sales list cannot be empty")
    List<Sale> sales,

    @NotNull(message = "Export format cannot be null")
    ExportFormat format
) {
}
