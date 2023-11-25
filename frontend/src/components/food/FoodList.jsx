import React from "react";
import { useGetAllCustomersQuery } from "../../api/usersSlice";
import { Link } from "react-router-dom";


export const FoodList = () => {
    const renderedUsers = (users) => users.map((user) => (
        
        <li key={user.userId}>
        <Link to={`/customer/${user.userId}`}>
            {user.firstName && user.lastName 
            ? `${user.firstName} ${user.lastName}`
            : user.firstName 
            ? user.firstName
            : user.lastName
            ? user.lastName
            : user.userName}</Link>
        </li>
    ));

    

    const { data, error, isLoading } = useGetAllCustomersQuery();

    return (
        <section>
            <h2>Customers</h2>
            {error ?(
            <>Oh no, there was an error</>
            ) : isLoading ? (
            <>Loading...</>
            ) : data ? (
            <>
            <ul>{renderedUsers(data)}</ul>
            </>
            ) : null}
        </section>
    )
}