import processing.core.PApplet;

// import java.io.FileInputStream;
// import java.io.IOException;

// import javax.print.Doc;
// import javax.print.DocFlavor;
// import javax.print.DocPrintJob;
// import javax.print.PrintException;
// import javax.print.PrintService;
// import javax.print.PrintServiceLookup;
// import javax.print.SimpleDoc;
// import javax.print.attribute.HashPrintRequestAttributeSet;
// import javax.print.attribute.PrintRequestAttributeSet;
// import javax.print.attribute.standard.Copies;
// import javax.print.attribute.standard.OrientationRequested;
// import javax.print.attribute.standard.MediaSizeName;

import javax.imageio.ImageIO;
import java.io.File;
// import javax.print.attribute.standard.PrinterResolution;
import java.awt.print.Paper;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.awt.geom.AffineTransform;

import java.util.List;


public class PrinterControl {
    private final String DEFAULT_PRINTER_NAME = "HiTi P525L";

    private PhotoBooth photoBooth;

    public PrinterControl(PhotoBooth aPhotoBooth) {
        this.photoBooth = aPhotoBooth;
    }

    // public boolean printFile(String filePath) {
    //     boolean isPrintSuccessful = true;

    //     // String[] params = { "lp", "-d", "Canon_iP110_series", filePath }; 
    //     // this.photoBooth.exec(params);
    //     // this.photoBooth.writeInfoLog("Printing " + filePath);       

    //     // http://www.rgagnon.com/javadetails/java-print-a-text-file-with-javax.print-api.html
    //     // http://stackoverflow.com/questions/16035739/how-to-access-the-status-of-the-printer

    //     // http://www.java2s.com/Code/Java/2D-Graphics-GUI/PrintanImagetoprintdirectly.htm
    //     try {
    //         PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    //         pras.add(new Copies(1));
    //         //pras.add(OrientationRequested.PORTRAIT);
    //         //pras.add(MediaSizeName.JAPANESE_POSTCARD);
    //         PrintService pss[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.GIF, pras);

    //         if (pss.length == 0) {
    //             throw new RuntimeException("No printer services available.");                
    //         }


    //         // Select printer service by printer name
    //         PrinterService ps = pss[0];
    //         StringBuilder printerList = new StringBuilder("Available printers:" + PhotoBooth.LINE_BREAK);
    //         for (int i = 0; i < pss.length; i++) {
    //             printerList.append("printer no." + i + " " + pss[i] + PhotoBooth.LINE_BREAK);

    //             if (pss[i].getName().indexOf(DEFAULT_PRINTER_NAME) > -1) {
    //                 ps = pss[i];
    //             }
    //         }
    //         photoBooth.writeInfoLog(printerList.toString());

            
    //         this.photoBooth.writeInfoLog("Printing to " + ps);
    //         DocPrintJob job = ps.createPrintJob();
    //         FileInputStream fin = new FileInputStream(filePath);
    //         Doc doc = new SimpleDoc(fin, DocFlavor.INPUT_STREAM.GIF, null);
    //         job.print(doc, pras);
    //         fin.close();         
    //     } catch (Exception ex) {
    //         isPrintSuccessful = false;
    //         this.photoBooth.handleGeneralException(ex);            
    //     }
     
    //     return isPrintSuccessful;
    // }

    public boolean printFile(String filePath) {
        photoBooth.writeInfoLog("Printer Controller printing " + filePath);

        boolean isPrintSuccessful = true;

        try {
            // List available printers
            StringBuilder printerList = new StringBuilder("Available printers:" + PhotoBooth.LINE_BREAK);

            PrinterUtility.refreshSystemPrinterList();
            List<String> printerServiceNames = PrinterUtility.getPrinterServiceNameList();

            if (printerServiceNames.size() > 0) {
                for (int i = 0; i < printerServiceNames.size(); i++) {
                    printerList.append("printer no." + i + " " + printerServiceNames.get(i) + PhotoBooth.LINE_BREAK);
                }
                photoBooth.writeInfoLog((printerList.toString()));
            } else {
                throw new Exception("No printers available.");       
            }


            // https://stackoverflow.com/questions/10594242/borderless-printing
            
            //PrinterJob pJob = PrinterJob.getPrinterJob();
            PrinterJob pJob = PrinterUtility.findPrinterJob(DEFAULT_PRINTER_NAME);
            photoBooth.println(pJob);

            final BufferedImage img = ImageIO.read(new File(filePath));

            // // Assuming that images are going to be 300 DPI
            // PrinterResolution pResolution = new PrinterResolution(300, 300,
            //     PrinterResolution.DPI);

            // PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            // pras.add(pResolution);

            // Set print job so the image name shows (in the print queue)
            pJob.setJobName(new File(filePath).getName());

            PageFormat pFormat = pJob.getPageFormat(null);
            Paper paper = pFormat.getPaper();
            //paper.setSize(4 * 72.0f, 6 * 72.0f);
            paper.setSize(4.133 * 72.0f, 6.147 * 72.0f);            
            paper.setImageableArea(
                0.0, 0.0,
                paper.getWidth(), paper.getHeight()
            );

            if (img.getWidth(null) > img.getHeight(null)) {
                pFormat.setOrientation(PageFormat.LANDSCAPE);
            } else {
                pFormat.setOrientation(PageFormat.PORTRAIT);
            }
            pFormat.setPaper(paper);

            // Create the page
            pJob.setPrintable(new Printable() {
                // public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                //     throws PrinterException {
                //     if (pageIndex != 0) {
                //         return Printable.NO_SUCH_PAGE;
                //     }

                //     double width = img.getWidth(null);
                //     double height = img.getHeight(null);

                //     double w = Math.floor(pageFormat.getImageableWidth() - 
                //         pageFormat.getImageableX()) / (width * 1.0);

                //     double h = Math.floor(pageFormat.getImageableHeight() - 
                //         pageFormat.getImageableY()) / (height * 1.0);

                //     double scale = Math.min(w, h);

                //     Graphics2D g2 = (Graphics2D) graphics;
                //     g2.translate(0, 0);
                //     g2.scale(scale, scale);
                //     g2.drawImage(img, 0, 0, (int)width, (int)height, null);

                //     return Printable.PAGE_EXISTS;                    
                // }

                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    Graphics2D g2 = (Graphics2D) graphics;
                    //final double xScale = 1.1;
                    final double xScale = 1;
                    //final double xTranslate = -55;
                    final double xTranslate = 0;
                    final double yScale = 1;
                    final double yTranslate = 0;
                    final double widthScale = (pageFormat.getWidth() / img.getWidth()) * xScale;
                    final double heightScale = (pageFormat.getHeight() / img.getHeight()) * yScale;
                    photoBooth.writeInfoLog("Printer Controller sets scale to " + widthScale + " x " + heightScale);
                    final AffineTransform at = AffineTransform.getScaleInstance(widthScale, heightScale);
                    photoBooth.writeInfoLog("Printer Controller sets translate to " + xTranslate + " x " + yTranslate);
                    at.translate(xTranslate, yTranslate);
                    if (pageIndex != 0) {
                        return NO_SUCH_PAGE;
                    }
                    g2.drawRenderedImage(img, at);
                    return PAGE_EXISTS;
                }
            }, pJob.validatePage(pFormat));

            // Get number of copies
            //int nCopies = SetPrintQuantity.getPrintQuantity(new File(image));
            int nCopies = 1;

            // Print
            if (nCopies != 0) {
                for (int i = 0; i < nCopies; i++) {
                    //pJob.print(pras);
                    pJob.print();
                }
            }

            photoBooth.writeInfoLog("Printer Controller " + nCopies + ((nCopies == 1) ? " copy" : " copies") + " printed.");
        } catch (Exception ex) {
            isPrintSuccessful = false;
        }

        return isPrintSuccessful;
    }
}