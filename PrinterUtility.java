import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.List;
import java.util.ArrayList;

// https://stackoverflow.com/questions/14885993/how-do-i-specify-the-printer-i-want-to-use-in-java
// https://stackoverflow.com/questions/11230502/how-to-use-java-to-print-on-a-network-printer/11232987#11232987
public final class PrinterUtility {

    /**
     * Retrieve a Print Service with a name containing the specified PrinterName; will return null if not found.
     * 
     * @return
     */
    public static PrintService findPrintService(String printerName) {

        printerName = printerName.toLowerCase();

        PrintService service = null;

        // Get array of all print services
        PrintService[] services = PrinterJob.lookupPrintServices();

        // Retrieve a print service from the array
        for (int index = 0; service == null && index < services.length; index++) {

            if (services[index].getName().toLowerCase().indexOf(printerName) >= 0) {
                service = services[index];
            }
        }

        // Return the print service
        return service;
    }

    /**
     * Retrieves a List of Printer Service Names.
     * 
     * @return List
     */
    public static List<String> getPrinterServiceNameList() {

        // get list of all print services
        PrintService[] services = PrinterJob.lookupPrintServices();
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < services.length; i++) {
            list.add(services[i].getName());
        }

        return list;
    }

    /**
     * Retrieve a PrinterJob instance set with the PrinterService using the printerName.
     * 
     * @return
     * @throws Exception IllegalStateException if expected printer is not found.
     */
    public static PrinterJob findPrinterJob(String printerName) throws Exception {

        // Retrieve the Printer Service
        PrintService printService = PrinterUtility.findPrintService(printerName);

        // Validate the Printer Service
        if (printService == null) {

            throw new IllegalStateException("Unrecognized Printer Service \"" + printerName + '"');
        }

        // Obtain a Printer Job instance.
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        // Set the Print Service.
        printerJob.setPrintService(printService);

        // Return Print Job
        return printerJob;
    }

    /**
     * Printer list does not necessarily refresh if you change the list of 
     * printers within the O/S; you can run this to refresh if necessary.
     */
    public static void refreshSystemPrinterList() {
        Class[] classes = PrintServiceLookup.class.getDeclaredClasses();
        for (int i = 0; i < classes.length; i++) {
            if ("javax.print.PrintServiceLookup$Services".equals(classes[i].getName())) {
                sun.awt.AppContext.getAppContext().remove(classes[i]);
                break;
            }
        }
    }

    /**
     * Utility class; no construction!
     */
     private PrinterUtility() {}
}