import React from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link } from 'react-router-dom'



export const Navbar = () => {
  const dispatch = useDispatch()

  // Trigger initial fetch of notifications and keep the websocket open to receive updates


  return (
    <nav className="navbar">
      <div className='nav-texts'>
        <Link to="/"> Home </Link>
        <Link to="/users"> Users </Link>
        <Link to="/posts/all-posts"> Posts </Link>
        <Link to="/posts/add-post"> Write Post </Link>
      </div>
    </nav>
  )
}
