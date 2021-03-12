package pl.edu.pw.mini.mg1.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IOUtilsTest {
    private static final String testFileContent =
        """
        TEST TEST TEST
        TEST FILE ONLY
        """;

    @Test
    public void readResource() {
        String fileContent = IOUtils.readResource("/test.txt");
        assertThat(fileContent).isEqualTo(testFileContent);
    }
}