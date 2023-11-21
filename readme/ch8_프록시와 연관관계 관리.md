프록시와 연관관계 관리
====================

## 1. 프록시
연관관계를 가진 엔티티를 EntityManager.find()로 불러올 때 실제로 사용하지 않는 연관관계의 데이터도 데이터베이스에서 조회하는데 이것은 효율적이지 않습니다.   
JPA에서는 실제 사용하기 전까지 데이터베이스 조회를 지연하는 방법을 제공하는데 이것을 지연 로딩이라합니다.   
지연 로딩 기능을 사용하려면 실제 엔티티 객체 대신에 객체 데이터베이스 조회를 지연할 수 있는 가짜 객체가 필요한데 이것을 프록시 객체라 합니다.   

**1-1. 프록시 기초**   
* 프록시 클래스는 실제 클래스를 상속 받아서 만들어져 모양은 같고 내용은 실제 객체의 참조를 가지고 있습니다. 만약 클래스 메소드 호출이 일어나면 이 참조를 바탕으로 실제 메소드 호출이 일어납니다.
* 클래스 메소드를 사용할때 데이터베이스를 조회하여 실제 객체를 생성하는걸 프록시 객체 초기화라 합니다.
* 프록시의 특징
  * 프록시 객체는 처음 사용할 때 한 번만 초기화된다.
  * 프록시 객체를 초기화 된다는건 프록시 객체를 통해 실제 엔티티에 접근할 수 있다는 것
  * 프록시 객체는 원본 엔티티를 상속받은 객체이므로 타입 체크시 주의해야한다.
  * 영속성 컨텍스트에 찾는 엔티티가 있으면 프록시가 아닌 실제 엔티티를 반환한다.
  * 준영속 상태의 프록시를 초기화하면 문제가 발생한다.

```java
// 준영속 상태와 초기화
Member member = em.getReference(Member.class, "id1");
transaction.commit();
em.close(); // 영속성 컨텍스트 종료

memeber.getName(); // 오류 발생 org.hibernate.LazyInitializationException
```

**1-2. 프록시와 식별자**   
엔티티를 프록시로 조회할때 식별자 값을 파라미터로 전달하는데 프록시 객체는 이 식별자 값을 보관한다.   
* @Access(AccessType.PROPERTY) : 식별자가 클래스 필드일 경우로 이때 프록시 객체에서 식별자 값을 가져올 때 프록시 객체 초기화가 일어나지 않는다.
* @Access(AccessType.FIELD) : 식별자가 클래스 접근자(GETTER)일 경우로 프록시 객체에서 식별자 값을 가져올 때 프록시 객체 초기화가 일어난다.

**1-3. 프록시 확인**   
JPA가 제공하는 PersistenceUnitUtil.isLoaded(Object entity) 메소드를 사용하여 프록시 인스턴스 초기화 여부를 확인할 수 있다. 
false는 초기화가 안된거고, true는 초기화 된것이다.
```java
boolean isLoad = em.getEntityManagerFactory(). getPersistenceUnitUtil().isLoaded(entity);
```
조회한 엔티티가 진짜 엔티티인지 프록시로 조회한지 확인하려면 클래스를 확인해보면 된다.
```java
sout("className = " + memeber.getClass().getName());
// ..javassist..라 나오면 프록시인걸 확인할 수 있는데 라이브러리에 따라 달라 질 수 있다.
```

## 2. 즉시 로딩과 지연 로딩
* 즉시 로딩 : 엔티티를 조회할 때 연관된 엔티티도 함께 조회한다.
  * @ManyToOne(fetch = FetchType.EAGER)
  * 즉시 로딩을 최적화하기 위해 가능하면 조인쿼리를 사용한다.

즉시 로딩시 외래 키가 null일 경우를 대비하여 left outer join을 사용하여 조회하는데 만약 외래 키가 null일리 없다면   
```java
// 방법 1
@JoinColumn(name="", nullable=false)
```
```java
// 방법 2
@ManyToOne(optional = false)
```
두가지 중 하나를 사용할 수 있다.
해당 기능을 사용하면 inner join을 사용하여 성능을 최적화 할 수 있다.

* 지연 로딩 : 연관된 엔티티를 실제 사용할 때 조회한다.
  * @ManyToOne(fetch = FetchType.LAZY)

각 연관관계 별 default는 다음과 같다.   
* OneToMany: LAZY 
* ManyToOne: EAGER 
* ManyToMany: LAZY 
* OneToOne: EAGER

## 3. 영속성 전이 : CASCADE
JPA에서 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야 한다.
```java
class Parent{
  ...
    @OneToMany(mapped="parent")
    private List<Child> children = new ArrayList<Child>();
  ...
}

class Child{
  ...
    @ManyToOne
    private Parent parent;
  ...
}

void test(){
    Parent parent = new Parent();
    em.persist();
    
    Child child1 = new Child();
    child1.setParent(parent);
    parent.getChildren().add(child1);
    em.persist(child1);

    Child child2 = new Child();
    child2.setParent(parent);
    parent.getChildren().add(child2);
    em.persist(child2);
}
```
위 코드를 영속성 전이:저장을 사용하면 간단하게 처리 가능하다.

```java
class Parent{
  ...
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Child> children = new ArrayList<Child>();
  ...
}

class Child{
  ...
}

void Test(){
    Child child1 = new Child();
    Child child2 = new Child();
    
    Parent parent = new Parent();
    child1.setParent();
    child2.setParent();
    parent.getChildren().add(child1);
    parent.getChildren().add(child2);
    
    em.persist(parent);
}
```
영속성 전이:삭제(CascadeType.REMOVE)를 사용하면 한번에 삭제 할 수 있다.

## 4. 고아 객체
부모 엔티티와 연관관계가 끊긴 자식 객체를 고아 객체라 한다.   
다음 옵션을 사용하면 부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제할 수 있다.
```java
class Parent{
  ...
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Child> children = new ArrayList<Child>();
  ...
}

class Child{
  ...
}

void test(){
    Parent parent = em.find(Parent.class, "pk");
    parent.getChildren().remove(0); // 플러시가 일어날 때 DB에 적용됨
}
```