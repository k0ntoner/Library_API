<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Registration Form</title></head>
<body>
<form action="/library/auth/registration" method="post">
    <label>Firstname: <input type="text" name="firstName" required /></label><br/>
    <label>Lastname: <input type="text" name="lastName" required /></label><br/>
    <label>Email: <input type="text" name="email" required /></label><br/>
    <label>Password: <input type="password" name="password" required /></label><br/>
    <label>Phone number: <input type="text" name="phoneNumber" required /></label><br/>

    <label>Type of user:
        <select name="userType" onchange="toggle()">
            <option value="customer">Customer</option>
            <option value="employee">Employee</option>
        </select>
    </label><br/>

    <div id="customer-fields">
        <label>Date of birth: <input type="date" name="dateOfBirth" /></label>
    </div>
    <div id="employee-fields" style="display:none;">
        <label>Salary: <input type="number" name="salary" step="0.01" /></label>
    </div>

    <button type="submit">Sign up</button>
</form>

<script>
    function toggle() {
        const type = document.querySelector('[name="userType"]').value;
        document.getElementById('customer-fields').style.display = type === 'customer' ? 'block' : 'none';
        document.getElementById('employee-fields').style.display = type === 'employee' ? 'block' : 'none';
    }
    toggle();
</script>
</body>
</html>
