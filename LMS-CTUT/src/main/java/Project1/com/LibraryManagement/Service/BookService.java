package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.Entity.Books;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Optional;


public interface BookService {
    public Books saveBooks(Books books);

    public List<Books> getAllBooks();

    public Boolean existsBooks(String bookCode, String bookName);

    @Transactional
    public Long deleteBooks(Long id);

    public Optional<Books> findById(Long id);

    public List<Books> findAllBook(String field);

    public Page<Books> findBookPagination(int offset, int pageSize);

    public Books getBookById(Long id);

    public List<String> suggest(String keyword);

    public void exportBooksToCsv(Writer writer) throws IOException;

    public void importBooksFromCsv(MultipartFile file) throws IOException;
    public void exportUsersToCsv(Writer writer) throws  IOException;
    Books findByBookCodeAndBookName(String bookCode, String bookName);


}
