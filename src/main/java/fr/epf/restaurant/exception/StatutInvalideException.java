package fr.epf.restaurant.exception;

public class StatutInvalideException extends AppException {
    public StatutInvalideException(String message) {
        super(400, message);
    }
}
