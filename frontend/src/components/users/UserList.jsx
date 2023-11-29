import React from "react";
import { useGetAllUsersQuery } from "../../api/usersSlice";
import { Link } from "react-router-dom";


export const UsersList = () => {
    const renderedUsers = (users) => users.map((user) => (
        <li key={user.id}>
        <Link to={`/users/${user.id}`}>
            {user.firstName && user.lastName 
            ? `${user.firstName} ${user.lastName}`
            : user.firstName 
            ? user.firstName
            : user.lastName
            ? user.lastName
            : user.userName}</Link>
        </li>
    ));

    const { data, error, isLoading } = useGetAllUsersQuery();

    return (
        <section>
            <h2>Users</h2>
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