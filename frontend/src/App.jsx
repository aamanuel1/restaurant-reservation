import { useState } from 'react'
import { UsersList } from './components/users/UserList'
import { BrowserRouter, Route, Router, Routes } from 'react-router-dom'
import { Navbar } from './components/Navbar'
import { UserPage } from './components/users/UserPage'
import { PostsList } from './components/posts/PostList'
import { SinglePostPage } from './components/posts/SinglePostPage'
import { AddPostForm } from './components/posts/AddPost'
import { EditPostForm } from './components/posts/EditPost'

const Home = () => {
  return (
    <div>
      <h2>
    Welcome to the HomePage!
      </h2>
    </div>
  )
}

function App() {

  return (
    <BrowserRouter>
      <Navbar />
      <div className='app'>
        <Routes>
          <Route path='/users' element={<UsersList />} />
          <Route path='/users/:userId' element={<UserPage />} />
          <Route path='/posts/all-posts' element={<PostsList />} />
          <Route path='/posts/:postId/:userId' element={<SinglePostPage />} />
          <Route path='/posts/add-post' element={<AddPostForm />} />
          <Route path='/edit-post/:postId/:userId' element={<EditPostForm />} />
          <Route path="/" element={<Home />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

export default App;
