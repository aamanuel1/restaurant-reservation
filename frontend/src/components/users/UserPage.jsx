import React from "react";
import { Link, useParams } from "react-router-dom";
import { useGetUserByIdQuery, useGetUserPostsQuery } from "../../api/usersSlice";


export const UserPage = () => {
    const { userId } = useParams();
    const { 
        data: user, 
        error: userError, 
        isLoading: userLoading
    } = useGetUserByIdQuery(userId);

    const { 
        data: userPosts, 
        error: postsError,
        isLoading: postsLoading
    } = useGetUserPostsQuery(userId);

    console.log("UserP: " + user);

    if (userLoading || postsLoading) {
        return <div>Loading Posts...</div>
    }

    if (userError ) {
        return <div>Error occurred : User</div>
    }

    if (postsError) {
        return <div>Error occurred when fetching Posts for User:</div>
    }

    let postsTitles;
    if (postsError) {
        postsTitles = (
            <div>
                <p>Error occurred when fetching Posts for User</p>
            </div>
        )
    } else if (userPosts === null) {
        postsTitles = (
            <div>
                <p>This user has no posts yet.</p>
            </div>
        )
    } else {
        postsTitles = userPosts.map((userPost) => (
            <li key={userPost.post.id}>
                <Link to={`/posts/${userPost.post.id}/${userPost.user.id}`}>{userPost.post.title}</Link>
            </li>
        ));
    }

    return (
        <section>
            <h2>{user ? user.fullName : "User not found"}</h2>
            <ul>{postsTitles}</ul>
        </section>
    )
}