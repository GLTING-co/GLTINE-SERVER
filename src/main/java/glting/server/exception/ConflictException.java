package glting.server.exception;

import lombok.Getter;

@Getter
public class ConflictException  extends CustomBaseException {
    public ConflictException(int httpStatus, String message, String code) {
        super(message, httpStatus, code);
    }
}
