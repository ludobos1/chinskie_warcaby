package com.ludobos1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

  @Autowired
  private BoardRepository boardRepository;

  public Board saveBoardState(Board board) {
    return boardRepository.save(board);
  }
  public List<Board> getAllBoards() {
    return boardRepository.findAll();
  }

  public Optional<Board> getBoardState(String id) {
    return boardRepository.findById(id);
  }
}
