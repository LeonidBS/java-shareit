package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles(profiles = "test")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
class ItemRequestDbServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final ItemMapper itemMapper;

    @Test
    void findAllExceptOwn() {
        User requestor = InstanceFactory.newUser(null, "name", "requestor@user.com");
        em.persist(requestor);
        User owner = InstanceFactory.newUser(null,"owner", "owner@user.com");
        em.persist(owner);
        User user = InstanceFactory.newUser(null,"user", "user@user.com");
        em.persist(user);
        ItemRequest sourceRequest = InstanceFactory.newItemRequest(null, "ItemRequest description",
                LocalDateTime.now(), requestor);
        em.persist(sourceRequest);
        List<ItemRequestDto> sourceRequestsDto = List.of(ItemRequestMapper.INSTANCE.mapToDto(sourceRequest));
        List<Item> items = List.of(InstanceFactory.newItem(null, "item", "good item",
                true, owner, sourceRequest));
        em.persist(items.get(0));
        sourceRequestsDto.get(0).setItems(itemMapper.mapListToItemDto(items));
        em.flush();

        List<ItemRequestDto> requestsTargetDto = itemRequestService.findAllExceptOwn(user.getId(), 0, 10);

        assertThat(requestsTargetDto, hasItem(allOf(
                hasProperty("id", equalTo(sourceRequest.getId())),
                hasProperty("description", equalTo(sourceRequest.getDescription())),
                hasProperty("created", equalTo(sourceRequest.getCreated())),
                hasProperty("items", equalTo(itemMapper.mapListToItemDto(items)))
        )));
    }
}
