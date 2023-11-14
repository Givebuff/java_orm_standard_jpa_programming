연관관계 매핑 기초
================
* 방향: 한쪽만 참조하는것을 단방향, 서로 참조하는것을 양방향이라 한다.
  * 단방향 : 팀 &rarr; 회원
  * 양방향 : 팀 &larr;&rarr; 회원
* 다중성 : 다대일, 일대다, 일대일, 다대다 관계가 있다.
* 연관관계의 주인 : 객체를 양뱡향 연관관계로 만들면 연관관계의 주인을 정해야한다.

## 1. 단방향 연관관계

```java
@Entity
public class Team{
    @Id
    @Column(name="TEAM_ID")
    private String id;
    
    private String name;
    //Getter, Setter
}

@Entity
public class Member{
    @Id
    @Column(name="MEMBER_ID")
    private String id;
    
    private String username;
    
    // 연관관계 매핑
    @ManyToOne
    @JoinColumn(name="TEAM_ID")
    private Team team;
    
    //연관관계 설정
    public void setTeam(Team team){
        this.team = team;
    }
    //Getter, Setter
}
```

**1-1. @JoinColumn**

| 속성                                        | 기능                      |기본값|
|-------------------------------------------|-------------------------|---|
| name                                      | 매핑할 외래 키 이름             ||
| referencedColumnName                      | 외래 키가 참조하는 대상 테이블의 컬럼명  ||
| foreignKey(DDL)                           | 외래 키 제약조건을 직접 지정할 수 있다. ||
| unique, nullable, columnDefinition, table | @Column의 속성과 같다.        ||

**1-2. @ManyToOne**

| 속성           | 기능                              | 기본값                                                      |
|--------------|---------------------------------|----------------------------------------------------------|
| optional     | false로 설정하면 연관된 엔티티가 항상 있어야한다.  | true                                                     |
| fetch        | 글로벌 페치 전략을 설정한다                 | @ManyToOne=FetchType.EAGER <br> OneToMany=FetchType.LAZY |
| cascade      | 영속성 전이 기능을 사용한다.                ||
| targetEntity | 연관된 엔티티의 타입 정보를 설정한다. 잘 사용하지 않음 ||

