package com.takeaway.mapper;

import com.takeaway.dto.UserDto;
import com.takeaway.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDTO(User user);

}
