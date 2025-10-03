
package org.java.observerPatternV1.observers;

import java.util.List;
import org.java.observerPatternV1.model.Sale;

/**
 * Observer interface for the Observer Pattern.
 * Classes implementing this interface will be notified when sales data is ready to be exported.
 */
public interface ReportObserver {
    /**
     * Called when sales data is ready to be exported.
     * 
     * @param sales the list of sales to export
     * @param outputDirectory the directory where the report should be saved
     */
    void update(List<Sale> sales, String outputDirectory);
}
