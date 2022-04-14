package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileParsingTests {
    private ClassLoader cl = FileParsingTests.class.getClassLoader();

    @Test
    void parsePdfTest() throws Exception {
        try (InputStream stream = getInputStream("5.zip", "pdf")) {
            PDF pdfParsed = new PDF(stream);
            Assertions.assertThat(pdfParsed.author).contains("Marc Philipp");
        }
    }

    @Test
    void zipXlsTest() throws Exception {
        try (InputStream stream = getInputStream("5.zip", "xlsx")) {
            XLS parsed = new XLS(stream);
            SoftAssertions.assertSoftly(
                    softAssertions -> {
                        softAssertions.assertThat(parsed.excel.getSheetAt(0).getPhysicalNumberOfRows()).isGreaterThan(1);
                        softAssertions.assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue().contains("test"));
                    }
            );
        }
    }

    @Test
    void zipCsvFile() throws Exception {
        try (InputStream stream = getInputStream("5.zip", "csv")) {
            Scanner in = new Scanner(stream);
            Assertions.assertThat(in.next()).contains("header");
        }
    }

    private InputStream getInputStream(String zipFilePath, String fileExtension) throws Exception {
        ZipFile zipFile = new ZipFile(new File(cl.getResource(zipFilePath).toURI()));
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        InputStream stream = null;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().contains(fileExtension)) {
                stream = zipFile.getInputStream(entry);
            }
        }
        return stream;
    }
}
