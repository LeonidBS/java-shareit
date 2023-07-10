package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero
    private Integer id;

    @Column
    @NotBlank(message = "Parameter name is empty")
    @Size(max = 200, message = "length of name is more then 200 symbols")
    private String name;

    @Column
    @NotBlank(message = "Email has not been passed")
    @Size(max = 320, message = "length of email is more then 320 symbols")
    @Email(message = "Format the passed email is wrong")
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "Format the passed email is not correct")
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId())
                && name.equals(((User) o).getName())
                && email.equals(((User) o).getEmail());
    }
}
