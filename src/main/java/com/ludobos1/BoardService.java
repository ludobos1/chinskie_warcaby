package com.ludobos1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

  @Autowired
  private BoardRepository boardRepository;

  public Board saveBoardState(Board board) {
    if (boardRepository == null) {
      System.out.println("BoardRepository is null!");
      return null;
    } else {
      System.out.println("BoardRepository injected successfully.");
    }
    return boardRepository.save(board);
  }

  public Optional<Board> getBoardState(String id) {
    return boardRepository.findById(id);
  }
}
