
import com.vayuVaani.models.File
import com.vayuVaani.models.FileType
import org.junit.Assert.assertTrue
import org.junit.Test

class FileTest {
    @Test
    fun equalsTest(){
        val file1= File("name1","path1", FileType.AUDIO,true)
        val file2= File("name2","path1", FileType.VIDEO,true)
        assertTrue(file1 == file2)
    }

}