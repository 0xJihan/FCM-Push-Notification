package com.jihan.app.domain.utils



fun validateUserCredentials(
        userName: String = "Default",
        email: String,
        password: String,
        confirmPassword:String=password
    ): Pair<Boolean, String> {

        val result = Pair(true, "")

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return Pair(false, "Please Provide All Required Information")
        } else if (email.isEmail()) {
            return Pair(false, "Invalid Email Address")
        } else if (password.length < 5) {
            return Pair(false, "Password should be at least 5 characters long")
        }else if(password!=confirmPassword){
            return Pair(false, "Password and Confirm Password should be same")
        }


        return result
    }



