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

## 2. 양방향 연관관계 매핑

```java
@Entity
public class Team {
    @Id
    @Column(name = "TEAM_ID")
    private String id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<Member>();
    // Getter, Setter
}

void biDirection() {
  Team team = em.find(Team.class, "team1");
  List<Member> members = team.getMembers();

  System.out.println("=================================================================");
  for(Member member:members){
    System.out.println("이름 : " + member.getUsername());
  }
}
```

## 3. 연관관계의 주인
테이블은 외래 키 하나로 두 테이블의 연관관계를 관리한다.   
엔티티를 단방향으로 매핑하면 참조를 하나만 사용함.   
엔티티를 양방향 관계로 설정하면 객체의 참조는 둘인데 외래 키는 하나다. 따라서 둘 사이에 차이가 발생한다.   
이런 차이로 JPA에서 두 객체 연관관계 중 하나를 정해서 테이블의 외래 키를 관리해야 하는데 이것을 연관관계의 주인이라 한다.   
연관관계의 주인만이 데이터베이스 연관관계와 매핑되고 외래 키를 관리할 수 있다. 반면에 주인이 아닌 쪽은 읽기만 할 수 있다.   
연관관계의 주인을 정한다는 것은 외래 키 관리자를 선택하는 것이다.

**3-1.mapped 속성**   
* 주인은 mapped 속성을 사용하지 않는다.
* 주인이 아니면 mapped 속성을 사용해서 속성의 값으로 연관관계의 주인을 지정해야 한다.

## 4.연관관계 편의 메소드 작성 시 주의사항
객체 기준으로 양뱡향 연관관계 코드 작성시 위의 코드를 그대로 사용하면 연관관계를 변경할 때 오류가 발생한다.   
그걸 해결하기 위해 아래와 같이 Setter에 validation 체크 코드를 작성하면 된다.
```java
public void setTeam(Team team){
    if(this.team != null){
        this.team.getMembers().remove(this);
    }
    this.team = team;
    team.getMembers().add(this);
}
```