import React, { useMemo } from "react";
import { useGetAllPostsQuery } from "../../api/usersSlice";
import { Link } from "react-router-dom";
import { Spinner } from "../Spinner";
import classnames from 'classnames';
import { TimeAgo } from "./TimeAgo";
import { ReactionButtons } from "./ReactionEmojis";


let PostExcerpt = ({ userPost }) => {
    return (
      <article className="post-excerpt" key={`${userPost.post.id}-${userPost.user.id}`}>
        <h3>{userPost.post.title}</h3>
        <div>
          <span>by: {userPost.user.fullName}</span>
          <TimeAgo timestamp={userPost.post.datePublished} />
        </div>
        <p className="post-content">{userPost.post.body.substring(0, 300)}</p>
  
        <Link to={`/posts/${userPost.post.id}/${userPost.user.id}`} className="button muted-button">
          View Post
        </Link>
        <ReactionButtons postId={userPost.post.id} />
      </article>
    );
  }

export const PostsList = () => {
  const {
      data,
      isLoading,
      isFetching,
      isSuccess,
      isError,
      error,
  } = useGetAllPostsQuery();

  // const sortedPosts = useMemo(() => {
  //     const sortedPosts = posts.slice();
  //     sortedPosts.sort((a, b) => b.datePublished.localeCompare(a.date));
  //     return sortedPosts
  // }, [posts])

  let content;

  if (isLoading) {
      content = <Spinner text="Loading.." />
  } else if (isSuccess) {
      const renderedPosts = data.map((userPost) => (
          <PostExcerpt key={`${userPost.post.id}-${userPost.user.id}`} userPost={userPost} />
      ))

      const containerClassname = classnames('posts-container', {
          disabled: isFetching,
      })

      content = <div className={containerClassname}>{renderedPosts}</div>
  } else if (isError) {
      content = <div>{error.toString}</div>
  }

  return (
      <section className="posts-list"> 
          <h2>Posts</h2>
          {content}
      </section>
  );

}