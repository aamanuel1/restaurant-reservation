import React from "react";
import { Link, useParams } from "react-router-dom";
import { useGetCustomerByEmailQuery } from "../../api/usersSlice";
import ReservationForm from "../forms/ReservationForm";


export const UserPage = () => {
    
    const { email } = useParams();
    console.log("UserEmail: "+ email)
    const { 
        data: user, 
        error: userError, 
        isLoading: userLoading
    } = useGetCustomerByEmailQuery(email);


    console.log("UserData: " + user);

    if (userLoading) {
        return <div>Loading your profile...</div>
    }

    if (userError ) {
        return <div>Error occurred : User</div>
    }

    let userInfo;
    if (user) {
        userInfo = (
            <div>
                <hr/>
                <p>{user ? `Name: ${user.firstName} ${user.lastName}` : "User not found"}</p>
                <p>{user ? `Email: ${user.email}` : ""}</p>
                <p>{user ? `Phone: ${user.phoneNum}` : ""}</p>
                <hr/>
                <Link style={{ display: 'block', margin: '10px 0' }} to={`/`}>Continue to make a Reservation</Link>
                <Link style={{ display: 'block', margin: '10px 0' }} to={`/food/list`}>View dishes</Link>
                <Link style={{ display: 'block', margin: '10px 0' }} to={`/restaurant/list`}>View Restaurants</Link>
                <hr/>
                
            </div>
        )
    }

    // let postsTitles;
    // if (postsError) {
    //     postsTitles = (
    //         <div>
    //             <p>Error occurred when fetching Posts for User</p>
    //         </div>
    //     )
    // } else if (userPosts === null) {
    //     postsTitles = (
    //         <div>
    //             <p>This user has no posts yet.</p>
    //         </div>
    //     )
    // } else {
    //     postsTitles = userPosts.map((userPost) => (
    //         <li key={userPost.post.id}>
    //             <Link to={`/posts/${userPost.post.id}/${userPost.user.id}`}>{userPost.post.title}</Link>
    //         </li>
    //     ));
    // }

    return (
        <section>
            {userInfo}
            {<ReservationForm />}
        </section>
    )
}