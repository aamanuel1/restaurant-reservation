import { useState } from 'react'
import { Home } from './components/HomePage'
import { UsersList } from './components/users/UserList'
import { BrowserRouter, Route, Router, Routes } from 'react-router-dom'
import { Navbar } from './components/Navbar'
import { UserPage } from './components/users/UserPage'
import { FoodList } from './components/food/FoodList'
import { RestaurantList } from './components/RestaurantList'


function App() {

  return (
    <BrowserRouter>
      <Navbar />
      <div className='app'>
        <Routes>
          <Route path='/customers' element={<UsersList />} />
          <Route path='/customer/:email' element={<UserPage />} />
          <Route path='/food/list' element={<FoodList />} />
          <Route path='/restaurant/list' element={<RestaurantList />} />
          {/* <Route path='/posts/all-posts' element={<PostsList />} />
          <Route path='/posts/:postId/:userId' element={<SinglePostPage />} />
          <Route path='/posts/add-post' element={<AddPostForm />} />
          <Route path='/edit-post/:postId/:userId' element={<EditPostForm />} /> */}
          <Route path="/" element={<Home />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

export default App;
