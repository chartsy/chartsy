package org.chartsy.main.utils;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import org.chartsy.main.managers.LoggerManager;

/**
 *
 * @author viorel.gheba
 */
public class Printer {

    private Printer() {}

    public static void print(Printable p, PageFormat pf) {
        if (p == null) return;
        if (pf == null) pf = new PageFormat();

        PrinterJob job = PrinterJob.getPrinterJob();

        Book book = new Book();
        book.append(p, pf);

        job.setPageable(book);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                LoggerManager.getDefault().log(ex);
            }
        }
    }

}
