package fr.epf.restaurant.exception;

public class StockInsuffisantException extends AppException {
    public StockInsuffisantException(String message) {
        super(400, message);
    }
}
