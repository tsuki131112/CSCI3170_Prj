package dataObject;

import repository.BookRepository;
import repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

public record BookOrder(String uid, String ISBN, int quantity) {

    public List<String> validate() {
        List<String> invalidReasons = new ArrayList<>();

        BookRepository bookRepository = new BookRepository();
        List<Book> books = bookRepository.findBookByISBN(ISBN);
        if (books.size() > 0) {
            int storage = books.get(0).quantity();
            if (storage < quantity) {
                invalidReasons.add("Inventory shortage, storage: " + storage + ", order: " + quantity);
            }
        } else {
            invalidReasons.add("Book ISBN cannot be found");
        }

        CustomerRepository customerRepository = new CustomerRepository();
        boolean userExists = customerRepository.checkUserExists(uid);

        if (!userExists) {
            invalidReasons.add("User does not exist");
        }

        if (uid.length() == 0) {
            invalidReasons.add("Invalid uid");
        }
        if (ISBN.length() == 0) {
            invalidReasons.add("Invalid ISBN");
        }
        if (quantity == 0) {
            invalidReasons.add("Invalid quantity");
        }

        return invalidReasons;
    }
}
