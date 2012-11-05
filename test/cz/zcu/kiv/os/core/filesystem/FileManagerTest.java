package cz.zcu.kiv.os.core.filesystem;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * FileManager test
 *
 * @author Jakub Danek
 */
public class FileManagerTest {
    
    private FileManager manager;

    @Before
    public void prepareTestClass() throws Exception {
        manager = new FileManager("/");
    }

    @After
    public void tearDownTestClass() throws Exception {
        manager = null;
    }

    /**
     * Tests absolute path resolving with directory jump.
     * @throws Exception
     */
    @Test
    public void testResolveAbsPathDirJump() throws Exception {
        String testPath = "/../Donald/Dolan/../Mickey/Minnie.c";
        String result = "/Donald/Mickey/Minnie.c";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/");

        assertEquals("Resolved path doesnt match the expected result!",result, resolved);
    }

    /**
     * Tests absolute path resolving with void directory path elements.
     * @throws Exception 
     */
    @Test
    public void testResolveAbsPathSameDir() throws Exception {
        String testPath = "//./Donald/Dolan/.////Mickey/./Minnie.c";
        String result = "/Donald/Dolan/Mickey/Minnie.c";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/");

        assertEquals("Resolved path doesnt match the expected result!",result, resolved);
    }

    /**
     * Tests absolute path resolving with directory jump and void path elements.
     * @throws Exception
     */
    @Test
    public void testResolveAbsPathCombined() throws Exception {
        String testPath = "/../Donald////./././Dolan/../Mickey/..././././////./Minnie.c";
        String result = "/Donald/Mickey/.../Minnie.c";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/");

        assertEquals("Resolved path doesnt match the expected result!",result, resolved);
    }

    /**
     * Tests relative path resolving with directory jump.
     * @throws Exception
     */
    @Test
    public void testResolveRelPathDirJump() throws Exception {
        String testPath = "./../Donald/Dolan/../Mickey/Minnie.c";
        String result = "/workingDir/workingSubdir/Donald/Mickey/Minnie.c";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/workingSubdir/ololo/");

        assertEquals("Resolved path doesnt match the expected result!",result, resolved);
    }

    /**
     * Tests relative path resolving with void path elements.
     * @throws Exception
     */
    @Test
    public void testResolveRelPathSameDir() throws Exception {
        String testPath = "././///////Donald/./././././Dolan/.///////Mickey/Minnie.c";
        String result = "/workingDir/workingSubdir/ololo/Donald/Dolan/Mickey/Minnie.c";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/workingSubdir/ololo/");

        assertEquals("Resolved path doesnt match the expected result!",result, resolved);
    }

    /**
     * Tests relative path resolving with directory jump and void path elements.
     * @throws Exception
     */
    @Test
    public void testResolveRelPathCombined() throws Exception {
        String testPath = "./////././//./////../Donald/Dolan/../Mickey/././././////././././././Minnie.c";
        String result = "/workingDir/workingSubdir/Donald/Mickey/Minnie.c";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/workingSubdir/ololo/");

        assertEquals("Resolved path doesnt match the expected result!",result, resolved);
    }

    @Test
    public void testNoDirPath() throws Exception {
        String testPath = "ahoj.txt";
        String result = "/workingDir/workingSubdir/ahoj.txt";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/workingSubdir/");
        assertEquals("Resolved path doesnt match the expected result!", result, resolved);
    }

    @Test
    public void testDirUpOnlyPath() throws Exception {
        String testPath = "..";
        String result = "/workingDir/";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/workingSubdir/");
        assertEquals("Resolved path doesnt match the expected result!", result, resolved);
    }

    @Test
    public void testCurrentDirOnlyPath() throws Exception {
        String testPath = ".";
        String result = "/workingDir/workingSubdir/";

        String resolved = manager.resolveRealPath(testPath, "/workingDir/workingSubdir/");
        assertEquals("Resolved path doesnt match the expected result!", result, resolved);

        testPath = "/";
        assertEquals("Resolved path doesnt match the expected result!", result, resolved);

    }

}
