import React, { useState } from 'react';
import { Spinner } from '../../components/Spinner';
import { useAddNewPostMutation, useGetAllUsersQuery } from '../../api/usersSlice';

export const AddPostForm = () => {
  const [title, setTitle] = useState('');
  const [body, setBody] = useState('');
  const [author, setAuthor] = useState(null);

  const [addNewPost, { isLoading }] = useAddNewPostMutation();
  const { data: users, error, isLoading: isUsersLoading } = useGetAllUsersQuery();

  const onTitleChanged = (e) => setTitle(e.target.value);
  const onContentChanged = (e) => setBody(e.target.value);
  const onAuthorChanged = (e) => {
    const selectedAuthor = users.find(user => user.id === e.target.value);
    setAuthor(selectedAuthor);
  }

  const canSave = [title, body, author].every(Boolean) && !isLoading;

  const onSavePostClicked = async () => {

    const datePublished = new Date();
    let post = {title: title, body: body, datePublished: datePublished};
    let userId = author.id;

    console.log(`NewPost22: ${post.title}`);
    console.log(`USERID: ${userId}`);

    if (canSave) {
      try {
        await addNewPost({post, userId}).unwrap();

        setTitle('');
        setBody('');
        setAuthor(null);
      } catch (err) {
        console.error('Failed to save the post: ', err)
      }
    }
  }

  const usersOptions = users ? users.map((author) => (
    <option key={author.id} value={author.id}>
      {author.fullName ? author.fullName : author.userName}
    </option>
  )) : [];

  const spinner = isLoading ? <Spinner size="30px" /> : null

  return (
    <section>
      <h2>Add a New Post</h2>
      <form>
        <label htmlFor="postTitle">Post Title:</label>
        <input
          type="text"
          id="postTitle"
          name="postTitle"
          placeholder="Title for your post..."
          value={title}
          onChange={onTitleChanged}
        />
        <label htmlFor="postAuthor">Author:</label>
        <select id="postAuthor" value={author ? author.id : ''} onChange={onAuthorChanged}>
          <option value=""></option>
          {usersOptions}
        </select>
        <label htmlFor="postContent">Content:</label>
        <textarea
          id="postContent"
          name="postContent"
          value={body}
          onChange={onContentChanged}
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