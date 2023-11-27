import React from "react";
import { useGetAllRestaurantsQuery } from "../api/usersSlice";
import { Link } from "react-router-dom";


export const RestaurantList = () => {
    const renderedList = (rests) => rests.map((rest) => (
        <li key={rest.restaurantId}>
            <p>{`Name: ${rest.name}`}</p>
            <p>{`Address: ${rest.location}  ${rest.postalCode}`}</p>
            <p>{`Cuisines: ${rest.cuisines}`}</p>
        </li>
    ));

    const { data, error, isLoading } = useGetAllRestaurantsQuery();
    return (
        <section>
            <h2>Restaurants</h2>
            {error ?(
            <>Oh no, there was an error</>
            ) : isLoading ? (
            <>Loading...</>
            ) : data ? (
            <>
            <ul>{renderedList(data)}</ul>
            </>
            ) : null}
        </section>
    )
}