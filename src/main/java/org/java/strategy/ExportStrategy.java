package org.java.strategy;

import java.util.List;
import org.java.model.Sale;

public interface ExportStrategy {

  void export(List<Sale> sales, String outputDirectory);
}
