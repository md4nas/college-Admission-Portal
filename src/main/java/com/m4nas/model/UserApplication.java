package com.m4nas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_applications")
public class UserApplication {

    @Id
    private String id;

    @Column(name = "user_email")
    private String userEmail;

    // Personal Info
    private LocalDate dob;
    private String gender;
    private String phoneNo;          // Changed to String
    private String address;
    private String religion;
    private String caste;
    private String city;
    private String state;
    private Integer pincode;         // Changed to Integer
    private String parentsName;
    private String parentsPhoneNo;   // Changed to String

    // Class 10 Marks
    private Integer passing10Year;
    private String schoolName10;
    private String board10Name;
    private String rollNo10;
    private Integer class10Math;
    private Integer class10Science;
    private Integer class10English;
    private Integer class10Hindi;
    private Integer class10Social;
    private Integer total10Marks;
    private Integer obtain10Marks;
    private Double percentage10;     // Changed to Double

    // Class 12 Marks
    private Integer passing12Year;
    private String schoolName12;
    private String board12Name;
    private String rollNo12;
    private Integer class12Physics;
    private Integer class12Chemistry;
    private Integer class12Maths;
    private Integer class12English;
    private Integer class12Optional;
    private Integer total12Marks;
    private Integer obtain12Marks;
    private Double percentage12;     // Changed to Double

    // Other entrance exam (optional)
    private String entranceName;
    private String entranceRollNo;
    private Integer entranceYear;
    private Integer entranceRank;

    // Preferences
    private String course;
    private String branch1;
    private String branch2;

    // Status
    private String allocatedBranch;
    private Boolean seatAccepted = false;
    private String status = "PENDING";
    private LocalDate submissionDate;

    // Constructor
    public UserApplication() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getCaste() { return caste; }
    public void setCaste(String caste) { this.caste = caste; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Integer getPincode() { return pincode; }
    public void setPincode(Integer pincode) { this.pincode = pincode; }

    public String getParentsName() { return parentsName; }
    public void setParentsName(String parentsName) { this.parentsName = parentsName; }

    public String getParentsPhoneNo() { return parentsPhoneNo; }
    public void setParentsPhoneNo(String parentsPhoneNo) { this.parentsPhoneNo = parentsPhoneNo; }

    // Class 10 getters/setters
    public Integer getPassing10Year() { return passing10Year; }
    public void setPassing10Year(Integer passing10Year) { this.passing10Year = passing10Year; }

    public String getSchoolName10() { return schoolName10; }
    public void setSchoolName10(String schoolName10) { this.schoolName10 = schoolName10; }

    public String getBoard10Name() { return board10Name; }
    public void setBoard10Name(String board10Name) { this.board10Name = board10Name; }

    public String getRollNo10() { return rollNo10; }
    public void setRollNo10(String rollNo10) { this.rollNo10 = rollNo10; }

    public Integer getClass10Math() { return class10Math; }
    public void setClass10Math(Integer class10Math) { this.class10Math = class10Math; }

    public Integer getClass10Science() { return class10Science; }
    public void setClass10Science(Integer class10Science) { this.class10Science = class10Science; }

    public Integer getClass10English() { return class10English; }
    public void setClass10English(Integer class10English) { this.class10English = class10English; }

    public Integer getClass10Hindi() { return class10Hindi; }
    public void setClass10Hindi(Integer class10Hindi) { this.class10Hindi = class10Hindi; }

    public Integer getClass10Social() { return class10Social; }
    public void setClass10Social(Integer class10Social) { this.class10Social = class10Social; }

    public Integer getTotal10Marks() { return total10Marks; }
    public void setTotal10Marks(Integer total10Marks) { this.total10Marks = total10Marks; }

    public Integer getObtain10Marks() { return obtain10Marks; }
    public void setObtain10Marks(Integer obtain10Marks) { this.obtain10Marks = obtain10Marks; }

    public Double getPercentage10() { return percentage10; }
    public void setPercentage10(Double percentage10) { this.percentage10 = percentage10; }

    // Class 12 getters/setters
    public Integer getPassing12Year() { return passing12Year; }
    public void setPassing12Year(Integer passing12Year) { this.passing12Year = passing12Year; }

    public String getSchoolName12() { return schoolName12; }
    public void setSchoolName12(String schoolName12) { this.schoolName12 = schoolName12; }

    public String getBoard12Name() { return board12Name; }
    public void setBoard12Name(String board12Name) { this.board12Name = board12Name; }

    public String getRollNo12() { return rollNo12; }
    public void setRollNo12(String rollNo12) { this.rollNo12 = rollNo12; }

    public Integer getClass12Physics() { return class12Physics; }
    public void setClass12Physics(Integer class12Physics) { this.class12Physics = class12Physics; }

    public Integer getClass12Chemistry() { return class12Chemistry; }
    public void setClass12Chemistry(Integer class12Chemistry) { this.class12Chemistry = class12Chemistry; }

    public Integer getClass12Maths() { return class12Maths; }
    public void setClass12Maths(Integer class12Maths) { this.class12Maths = class12Maths; }

    public Integer getClass12English() { return class12English; }
    public void setClass12English(Integer class12English) { this.class12English = class12English; }

    public Integer getClass12Optional() { return class12Optional; }
    public void setClass12Optional(Integer class12Optional) { this.class12Optional = class12Optional; }

    public Integer getTotal12Marks() { return total12Marks; }
    public void setTotal12Marks(Integer total12Marks) { this.total12Marks = total12Marks; }

    public Integer getObtain12Marks() { return obtain12Marks; }
    public void setObtain12Marks(Integer obtain12Marks) { this.obtain12Marks = obtain12Marks; }

    public Double getPercentage12() { return percentage12; }
    public void setPercentage12(Double percentage12) { this.percentage12 = percentage12; }

    // Entrance exam getters/setters
    public String getEntranceName() { return entranceName; }
    public void setEntranceName(String entranceName) { this.entranceName = entranceName; }

    public String getEntranceRollNo() { return entranceRollNo; }
    public void setEntranceRollNo(String entranceRollNo) { this.entranceRollNo = entranceRollNo; }

    public Integer getEntranceYear() { return entranceYear; }
    public void setEntranceYear(Integer entranceYear) { this.entranceYear = entranceYear; }

    public Integer getEntranceRank() { return entranceRank; }
    public void setEntranceRank(Integer entranceRank) { this.entranceRank = entranceRank; }

    // Preferences getters/setters
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getBranch1() { return branch1; }
    public void setBranch1(String branch1) { this.branch1 = branch1; }

    public String getBranch2() { return branch2; }
    public void setBranch2(String branch2) { this.branch2 = branch2; }

    // Status getters/setters
    public String getAllocatedBranch() { return allocatedBranch; }
    public void setAllocatedBranch(String allocatedBranch) { this.allocatedBranch = allocatedBranch; }

    public Boolean getSeatAccepted() { return seatAccepted; }
    public void setSeatAccepted(Boolean seatAccepted) { this.seatAccepted = seatAccepted; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }
}
