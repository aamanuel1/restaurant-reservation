import React, { useState } from 'react';
import { Spinner } from '../../components/Spinner';
import { useEditPostMutation, useGetPostByIdQuery } from '../../api/usersSlice';
import { useNavigate, useParams } from 'react-router-dom';

export const EditPostForm = () => {

    const navigate = useNavigate();

    const { postId, userId } = useParams();
    const { data: oldPost, isFetching, isSuccess } = useGetPostByIdQuery(postId);

  const [title, setTitle] = oldPost ? useState(oldPost.title) : useState('');
  const [body, setBody] = oldPost ? useState(oldPost.body) : useState('');

  const [updatePost, { isLoading }] = useEditPostMutation();

  const onTitleChanged = (e) => setTitle(e.target.value);
  const onContentChanged = (e) => setBody(e.target.value);

  const canSave = [title, body].every(Boolean) && !isFetching && !isLoading;

  const onSavePostClicked = async () => {

    let post = {id: oldPost.id, title: title, body: body, datePublished: oldPost.datePublished};

    console.log("post id" + postId);
    console.log("user id" + userId);
    if (canSave) {
      try {
        await updatePost({ post });
        navigate(`/posts/${postId}/${userId}`);
      } catch (err) {
        console.error('Failed to save the post: ', err);
      }
    }
  }


  const spinner = isLoading ? <Spinner size="30px" /> : null

  return (
    <section>
      <h2>Editing Post</h2>
      <form>
        <label htmlFor="postTitle">Post Title:</label>
        <input
          type="text"
          id="postTitle"
          name="postTitle"
          placeholder="Title for your post..."
          value={title}
          onChange={onTitleChanged}
          disabled={isLoading}
        />
        <label htmlFor="postContent">Content:</label>
        <textarea
          id="postContent"
          name="postContent"
          value={body}
          onChange={onContentChanged}
          disabled={isLoading}
          edit-area="true"
        />
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
          }}
        >
          <button type="button" onClick={onSavePostClicked} disabled={!canSave}>
            Save Post
          </button>
          {spinner}
        </div>
      </form>
    </section>
  )
}