## 翻译差异事项

本文件记录了火车调度系统cpp版本和Java版本在代码结构和实现上的差异,包括但不限于语法,数据结构,数据存储等方面.
方便后续理解和维护.

缓存


### 代码部分差异
1. **java有垃圾回收器,自动垃圾回收机制,不需要写析构函数**,例如:
```cpp
//cpp
PurchaseInfo() = default;
PurchaseInfo(const UserID userID, const TrainID &trainID, const Date &date, const StationID &departureStation, int type) {
    this->userID = userID;
    this->trainID = trainID;
    ... //省略其他成员变量
}
~PurchaseInfo() = default; //这个就是析构函数

```
```java
//java
public PurchaseInfo() {
    // 默认构造函数
}
// java没有析构函数,垃圾回收器会自动回收不再使用的
public PurchaseInfo(String userID, String trainID, Date date, String departureStation, int type) {
    this.userID = userID;
    this.trainID = trainID;
    //... 省略其他成员变量
}
```

2. **java没有引用传递,只有值传递,所以函数参数传递时,如果是对象,传递的是对象的引用的值,而不是对象本身**,例如:
```cpp
void addUser(const UserID userID, const Password &password) {
    if (userTable.find(userID) != userTable.end()) {
        throw "User already exists";
    }
    userTable[userID] = User(userID, password);
}
```
```java
public void addUser(String userID, String password) {
    if (userTable.containsKey(userID)) {
        throw new IllegalArgumentException("User already exists");
    }
    userTable.put(userID, new User(userID, password));
}
```
3. **cpp成员函数的const修饰符,表示该函数不会修改类的成员变量,而java没有这个修饰符,改为在成员变量前添加"final"**,例如:
```cpp

bool isUserExist(const UserID userID) const {
    return userTable.find(userID) != userTable.end();
}
```
```java
private final Map<String, User> userTable = new HashMap<>();
public boolean isUserExist(String userID) {
    return userTable.containsKey(userID);
}
```
4. 

### 结构部分差异

1. **java14新增记录类,我们在当中也是用了记录类以方便阅读**
```java
public record StationInfo(String stationName, int arrivalTime, int departureTime, int stopoverTime, int distance, int price) {}
```
*等同于*
```java
public class StationInfo {
    private final String stationName;
    private final int arrivalTime;
    private final int departureTime;
    private final int stopoverTime;
    private final int distance;
    private final int price;

    public StationInfo(String stationName, int arrivalTime, int departureTime, int stopoverTime, int distance, int price) {
        this.stationName = stationName;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopoverTime = stopoverTime;
        this.distance = distance;
        this.price = price;
    }

    public String getStationName() {
        return stationName;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public int getStopoverTime() {
        return stopoverTime;
    }

    public int getDistance() {
        return distance;
    }

    public int getPrice() {
        return price;
    }
}
```
2. **comparable<T>接口和comparator接口的使用,cpp中重载了<运算符,而java中实现了comparable接口的compareTo方法**,例如:
```java
```

3. **本项目中的所有的getter/setter方法均用lombok插件自动生成以提高可读性与开发速度**,例如:
```java
@Getter
@Setter
public class User {
    private String userID;
    private String password;
    private boolean isAdmin;
}
```
_等同于_
```java
public class User {
    private String userID;
    private String password;
    private boolean isAdmin;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
```

### 数据存储部分差异
1. **cpp中用map存储用户信息,而java中用hashmap存储用户信息**,例如:
```cpp
//cpp
std::map<UserID, User> userTable;
```
```java
//java
private final Map<String, User> userTable = new HashMap<>();
```
2. **cpp中用vector存储车次信息,而java中用list存储车次信息**,例如:
```cpp
//cpp
std::vector<Train> trainTable;
```
```java
//java
private final List<Train> trainTable = new ArrayList<>();
```
3. **cpp中用vector存储车票信息,而java中用list存储车票信息**,例如:
```cpp
//cpp
std::vector<PurchaseInfo> purchaseTable;
```
```java
//java
private final List<PurchaseInfo> purchaseTable = new ArrayList<>();
```
4. **cpp中用vector存储车票信息,而java中用list存储车票信息**,例如:
```cpp
//cpp
std::vector<StationInfo> stationTable;
```
```java
//java
private final List<StationInfo> stationTable = new ArrayList<>();
```
5. **cpp中用vector存储车票信息,而java中用list存储车票信息**,例如:
```cpp
//cpp
std::vector<StationInfo> stationTable;
```
```java
//java
private final List<StationInfo> stationTable = new ArrayList<>();
```
6. **cpp中用vector存储车票信息,而java中用list存储车票信息**,例如:
```cpp
//cpp
std::vector<StationInfo> stationTable;
```
```java
//java
private final List<StationInfo> stationTable = new ArrayList<>();
```
7. **cpp中用vector存储车票信息,而java中用list存储车票信息**,例如:

