package com.animeflict.todo_app.service;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<?> getSalesReport(LocalDate from, LocalDate to);
    List<?> getDueReport();
    List<?> getLowStockReport();
}
