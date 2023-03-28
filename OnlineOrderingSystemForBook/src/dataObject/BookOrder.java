package dataObject;

import repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

public record BookOrder(String uid, String ISBN, int quantity) {

    public List<String> validate() {
        List<String> invalidReasons = new ArrayList<>();

        CustomerRepository customerRepository = new CustomerRepository();
        List<Book> books = customerRepository.findBookByISBN(ISBN);
        if (books.size() > 0) {
            int storage = books.get(0).quantity();
            if (storage < quantity) {
                invalidReasons.add("Inventory shortage, storage: " + storage + ", order: " + quantity);
            }
        } else {
            invalidReasons.add("Book ISBN cannot be found");
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
