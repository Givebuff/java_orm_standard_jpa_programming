값 타입
===================

### 1. JPA 값 타입
* 엔티티 타입(@Entity)
  * 식별자가 있다
  * 생명 주기가 있다.(영속성)
  * 공유할 수 있다.
* 값 타입(int, String, Integer 등)
  * 식별자가 없다
  * 생명 주기를 엔티티에 의존한다.
  * 공유하지 않는 것이 안전하다.
    * 잘못 공유시 공유 참조로 인해 사이드 이펙트가 일어나고 오류 발생시 확인하기 힘들다.
    * 공유 참조를 해결하기 위해선 생성자로만 데이터를 설정할 수 있게하고 Setter를 만들지 않는 방법이 있다.

### 2. 값 타입

**2-1. 기본 값 타입**
* 자바 기본 타입 - int, double
* 래퍼 클래스 - Integer
* String

**2-2. 임베디드 타입(복합 값 타입)**   
* 임베디드 타입은 기본 생성자가 필수 입니다.
```java
class Member {
    ...
    //근무 기간
    @Temporal(TemporalType.DATE)
    Date startDate;
    @Temporal(TemporalType.DATE)
    Date endDate;
    
    //주소
    private String city;
    private Striing street;
    private String zipcode;
    ...
}
```
엔티티 선언시 위와 같이 선언할 수 있는데 이때 데이터를 그룹으로 묶을 수 있다.

```java
class Member{
    ...
    @Embedded
    Period workPeriod;
    @Embedded
    @AttributeOverrides({
        @AttributeOverrride(name="city", column=@Column(name="MEMBER_CITY")),
        @AttributeOverrride(name="street", column=@Column(name="MEMBER_STREET")),
        @AttributeOverrride(name="state", column=@Column(name="MEMBER_STATE")),
    })
    Address homeAddress;
    ...
}

@Embeddable
class Period{
    ...
    @Temporal(TemporalType.DATE)
    Date startDate;
    @Temporal(TemporalType.DATE)
    Date endDate;
    ...
}

@Embeddable
class Address{
    ...
    private String city;
    private Striing street;
    private String zipcode;
    ...
}
```
위와 같이 사용할 수 있고 @Embedded, @Embeddable 둘 중 하나는 생략해도 된다.

### 3. 값 타입 비교
* 동일성 비교 : 인스턴스 참조 값 비교(reference), == 사용
* 동등성 비교 : 인스턴스의 값 비교(value), equals() 사용   

임베디드 타입 등 신규 타입들은 비교를 위해 equals를 새로 정의 해줘야 한다.

### 4. 값 타입 컬렉션
값 타입 하나 이상 저장하려면 @ElementCollection, @CollectionTable을 사용하면 된다.   
그렇게 저장하면 1:N형식으로 데이터를 저장할 수 있다.