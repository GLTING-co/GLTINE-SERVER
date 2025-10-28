package glting.server.users.controller.vo.request;

public class UserRequest {
    public record NoAccountRequest(
            String type,
            String id,
            String name,
            String birth,
            String gender,
            String sexualType,
            String relationship
    ) {
    }
}
