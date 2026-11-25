package com.sprint.lending.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectLoanRequest {
    @NotBlank(message = "Rejection remarks cannot be empty.")
    @Size(max = 500, message = "Rejection remarks cannot exceed 500 characters")
    private String remarks;

    public RejectLoanRequest(){

    }

    public String getRemarks(){
        return remarks;
    }

    public void setRemarks(String remarks){
        this.remarks = remarks;
    }

}
