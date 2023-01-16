package nextstep.member;

public abstract class MemberBaseDto {

    private final Long id;
    private final String username;
    private final String name;

    public MemberBaseDto(Long id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

}
