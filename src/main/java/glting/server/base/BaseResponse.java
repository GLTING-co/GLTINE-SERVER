package glting.server.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static glting.server.base.BaseUtil.SUCCESS;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
final public class BaseResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    public static <T> BaseResponse<T> ofSuccess(int status, T data) {
        return new BaseResponse<>(status, SUCCESS, data);
    }

    public static <T> BaseResponse<T> ofFail(int status, String message) {
        return new BaseResponse<>(status, message, null);
    }
}
