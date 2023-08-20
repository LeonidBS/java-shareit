package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoInput {
    private Integer id;

    @NotBlank(message = "Parameter name is empty", groups = ValidationGroups.Create.class)
    private String name;

    @NotBlank(message = "Parameter name is empty", groups = ValidationGroups.Create.class)
    @Email(message = "Format the passed email is wrong")
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "Format the passed email is not correct")
    private String email;
}
