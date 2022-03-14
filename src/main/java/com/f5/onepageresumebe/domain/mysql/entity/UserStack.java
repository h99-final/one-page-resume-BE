package com.f5.onepageresumebe.domain.mysql.entity;

import lombok.*;

import javax.persistence.*;


@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "stack_id")
    private Stack stack;

    @Builder(access = AccessLevel.PRIVATE)
    public UserStack(User user, Stack stack) {
        this.user = user;
        this.stack = stack;
    }

    public static UserStack create(User user, Stack stack){
        UserStack userStack = UserStack.builder()
                .user(user)
                .stack(stack)
                .build();

        user.getUserStackList().add(userStack);
        stack.getUserStackList().add(userStack);

        return userStack;
    }

}
