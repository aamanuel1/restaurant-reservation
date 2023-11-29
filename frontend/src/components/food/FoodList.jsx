import React from "react";
import { useGetAllFoodsQuery } from "../../api/usersSlice";
import { Link } from "react-router-dom";


export const FoodList = () => {
    const renderedUsers = (foods) => foods.map((food) => (
        
        <li key={food.id}>
            {food.name && food.cuisine ? `${food.name} -- ${food.cuisine}` : food.name }
        </li>
    ));

    

    const { data, error, isLoading } = useGetAllFoodsQuery();

    return (
        <section>
            <h2>Dishes</h2>
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