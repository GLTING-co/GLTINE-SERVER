package glting.server.users.controller.vo.response;

import glting.server.users.entity.UserEntity;
import java.time.LocalDate;

public record UserProfileResponse(
        Long userSeq,
        String name,
        Integer age,
        String gender,
        String sexualType,
        String relationship,
        String image
) {

//    public static UserProfileResponse from(UserEntity user,String image) {
//        return new UserProfileResponse(
//                user.getUserSeq(),
//                user.getName(),
//                user.getGender(),
//                user.getSexualType(),
//                user.getRelationship(),
////                calculateAge(user.getBirth()),
//                image
//        );
//    }
//
//    private static int calculateAge(String birth) {
//        // birth: "YYYY-MM-DD" 형태일 때
//        int birthYear = Integer.parseInt(birth.substring(0, 4));
//        return LocalDate.now().getYear() - birthYear;
//    }
}
