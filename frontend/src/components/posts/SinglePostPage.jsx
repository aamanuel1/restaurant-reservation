import React from "react";
import { useParams } from "react-router-dom";
import { useGetPostByIdAndUserIdQuery } from "../../api/usersSlice";
import { Spinner } from "../Spinner";
import { TimeAgo } from "./TimeAgo";
import { ReactionButtons } from "./ReactionEmojis";
import { Link } from "react-router-dom";


export const SinglePostPage = () => {

    // const params = useParams();
    // const { data: userPost, isFetching, isSuccess } = useGetPostByIdAndUserIdQuery({...params});

    const { postId, userId } = useParams();
    const { data: userPost, isFetching, isSuccess } = useGetPostByIdAndUserIdQuery({ postId, userId });

    let content;
    if (isFetching) {
        content = <Spinner text="Loading post" />
    } else if (isSuccess) {
        content = (
            <article className="post">
                <h2>{userPost.post.title}</h2>
                <Link to={`/edit-post/${userPost.post.id}/${userPost.user.id}`} className="button muted-button">
                Edit Post
                </Link>
                <div>
                    <span>by: {userPost.user.fullName}</span>
                    <TimeAgo timestamp={userPost.post.datePublished} />
                </div>
                <p className="post-content">
                    {userPost.post.body}
                </p>
                <ReactionButtons postId={postId} />
            </article>
        )
    }

    return (
        <section>{content}</section>
    )
}